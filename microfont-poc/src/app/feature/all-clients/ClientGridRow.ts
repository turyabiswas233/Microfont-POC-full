import { ClientRecord } from '../client-registration/service/client-registration.service';

type ClientGridRow = {
  clientName: string;
  clientId: number;
  fatherName: string;
  motherName: string;
  gender: string;
  dateOfBirth: string;
  maritalStatus: string;
  mobileNumber: string;
  email: string;
  addressType: string;
  city: string;
  zipCode: string;
  nidNumber: string;
  officeCode: string;
  accountNumber: string;
  accountTitle: string;
  accountOpenDate: string;
  accountExpiryDate: string;
  limitAmount: number;
};

const CLIENT_GRID_FIELDS = {
  clientId: 'Client ID',
  clientName: 'Client Name',
  fatherName: 'Father Name',
  motherName: 'Mother Name',
  gender: 'Gender',
  dateOfBirth: 'Date of Birth',
  maritalStatus: 'Marital Status',
  mobileNumber: 'Mobile Number',
  addressType: 'Address Type',
  email: 'Email',
  city: 'City',
  zipCode: 'Zip Code',
  nidNumber: 'NID Number',
  officeCode: 'Office Code',
  accountNumber: 'Account Number',
  accountTitle: 'Account Title',
  accountOpenDate: 'Account Open Date',
  accountExpiryDate: 'Account Expiry Date',
  limitAmount: 'Limit Amount',
} as const satisfies Record<Exclude<keyof ClientGridRow, 'id'>, string>;
class ClientGridService {
  constructor() {}
  static toGridRow(item: ClientRecord): ClientGridRow {
    return {
      clientName: item.retrieveClientInfo.clientName ?? '',
      clientId: item.retrieveClientInfo.clientId ?? -1,
      fatherName: item.retrieveClientDetails.fatherName ?? '',
      motherName: item.retrieveClientDetails.motherName ?? '',
      gender: item.retrieveClientDetails.gender ?? '',
      dateOfBirth: item.retrieveClientDetails.dateOfBirth ?? '',
      maritalStatus: item.retrieveClientDetails.maritalStatus ?? '',
      addressType: item.retrieveClientAddress.addressType ?? '',
      mobileNumber: item.retrieveClientAddress.mobileNumber ?? '',
      email: item.retrieveClientAddress.email ?? '',
      city: item.retrieveClientAddress.city ?? '',
      zipCode: item.retrieveClientAddress.zipCode ?? '',
      nidNumber: item.retrieveClientDetails.nidNumber ?? '',
      officeCode: item.retrieveClientAccountInfo.officeCode ?? '',
      accountNumber: item.retrieveClientAccountInfo.accountNumber ?? '',
      accountTitle: item.retrieveClientAccountInfo.accountTitle ?? '',
      accountOpenDate: item.retrieveClientAccountInfo.accountOpenDate ?? '',
      accountExpiryDate: item.retrieveClientAccountInfo.accountExpiryDate ?? '',
      limitAmount: item.retrieveClientAccountInfo.limitAmount ?? 0,
    };
  }
}
export { type ClientGridRow, CLIENT_GRID_FIELDS, ClientGridService };
